package com.xpf.compiler;


import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.xpf.annotation.ARouter;
import com.xpf.annotation.model.RouterBean;
import com.xpf.compiler.utils.Constants;
import com.xpf.compiler.utils.EmptyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


// AutoService则是固定的写法，加个注解即可
// 通过auto-service中的@AutoService可以自动生成AutoService注解处理器，用来注册
// 用来生成 META-INF/services/javax.annotation.processing.Processor 文件
@AutoService(Processor.class)
@SupportedAnnotationTypes({Constants.ROUTER_ANNOTATION_TYPE})
// 允许/支持的注解类型全类名，让注解处理器处理（新增annotation module）
@SupportedSourceVersion(SourceVersion.RELEASE_7)// 指定JDK编译版本
@SupportedOptions({Constants.MODULE_NAME, Constants.APT_PACKAGE})// 注解处理器接收的参数
public class ARouterProcessor extends AbstractProcessor {

    // 操作Element工具类 (类、函数、属性都是Element)
    private Elements elementUtils;

    // type(类信息)工具类，包含用于操作TypeMirror的工具方法
    private Types typeUtils;

    // 用来报告错误，警告和其他提示信息
    private Messager messager;

    // 文件生成器 类/资源，Filter用来创建新的源文件，class文件以及辅助文件
    private Filer filer;

    // 子模块名，如：app/order/personal，需要拼接类名时用到(必须传)ARouter$$Group$$order
    private String moduleName;

    // 包名，用于存放APT生成的类文件
    private String packageNameForAPT;

    // 临时map存储，用来存放路由组Group对应的详细Path类对象，生成路由路径类文件时遍历
    // key:组名"app", value:类名"ARouter$$Path$$app.class"
    private Map<String, List<RouterBean>> tempPathMap = new HashMap<>();

    // 临时map存储，用来存放路由Group信息，生成路由组类文件时遍历
    // key:组名"app", value:类名"ARouter$$Path$$app.class"
    private Map<String, String> tempGroupMap = new HashMap<>();

    // 该方法主要用于一些初始化的操作，通过该方法的参数ProcessingEnvironment可以获取一些列有用的工具类
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        // 父类受保护属性，可以直接拿来使用。
        // 其实就是init方法的参数ProcessingEnvironment
        // processingEnv.getMessager(); //参考源码64行
        elementUtils = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        typeUtils = processingEnv.getTypeUtils();

        //通过ProcessingEnvironment去获取build.gradle(app module)传过来的参数值
        Map<String, String> options = processingEnv.getOptions();
        if (!EmptyUtils.isEmpty(options)) {
            moduleName = options.get(Constants.MODULE_NAME);
            packageNameForAPT = options.get(Constants.APT_PACKAGE);
            // 有坑：Diagnostic.Kind.ERROR，异常会自动结束，不像安卓中Log.e那么好使
            messager.printMessage(Diagnostic.Kind.NOTE, "moduleName >>> " + moduleName);
            messager.printMessage(Diagnostic.Kind.NOTE, "packageName >>> " + packageNameForAPT);
        }

        //必传参数判空(乱码问题：添加java控制台输出中文乱码)
        if (EmptyUtils.isEmpty(moduleName) || EmptyUtils.isEmpty(packageNameForAPT)) {
            throw new RuntimeException("注解处理器需要的参数moduleName或者packageName为空，请在对应build.gradle配置参数");
        }

    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        //一旦有类上面使用了@ARouter注解
        if (!EmptyUtils.isEmpty(set)) {
            //获取所有被@ARouter注解的元素集合
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ARouter.class);
            if (!EmptyUtils.isEmpty(elements)) {
                //解析元素
                try {
                    parseElements(elements);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //坑！！！
            return true;
        }
        return false;
    }

    /**
     * 解析所有被@ARouter注解的元素集合
     */
    private void parseElements(Set<? extends Element> elements) {
        //通过Element工具类，获取Activity类型
        TypeElement activityType = elementUtils.getTypeElement(Constants.ACTIVITY);
        //通过Element工具类，获取Callback类型
        TypeElement callType = elementUtils.getTypeElement(Constants.CALL);
        //显示(Activity)类信息
        TypeMirror activityMirror = activityType.asType();
        //显示回调接口的类信息
        TypeMirror callMirror = callType.asType();

        //循环遍历是因为app,order,personal不同模块有多个activity被@ARouter注解
        for (Element element : elements) {
            //获取每个元素的类信息
            TypeMirror elementMirror = element.asType();
            messager.printMessage(Diagnostic.Kind.NOTE, "遍历的元素信息为：" + elementMirror.toString());
            //获取每个类上的@ARouter注解，对应的path值
            ARouter aRouter = element.getAnnotation(ARouter.class);
            //路由详细信息，封装到实体类
            RouterBean bean = new RouterBean.Builder()
                    .setGroup(moduleName)
                    .setPath(aRouter.path())
                    .setElement(element)
                    .build();

            //高级判断，@ARouter注解仅仅只能用在类之上，并且规定是Activity
            // isSubtype方法，表示elementMirror 是activityMirror的类型或子类型时返回true
            if (typeUtils.isSubtype(elementMirror, activityMirror)) {
                bean.setType(RouterBean.Type.ACTIVITY);
            } else if(typeUtils.isSubtype(elementMirror,callMirror)) {
                bean.setType(RouterBean.Type.CALL);
            } else {
                throw new RuntimeException("@ARouter注解目前仅限用于Activity之上...");
            }
            //赋值临时map存储以上信息，用来遍历时生成代码
            valueOfPathMap(bean);
        }
        //ARouterLoadGroup和ARouterLoadPath类型，用来生成类文件时实现接口
        TypeElement groupLoadType = elementUtils.getTypeElement(Constants.ROUTER_GROUP);
        TypeElement pathLoadType = elementUtils.getTypeElement(Constants.ROUTER_PATH);

        //1.生成路由的详细Path类文件，如：ARouter$$Path$$app
        try {
            createPathFile(pathLoadType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //2.生成路由组Group类文件(没有Path类文件则Group类文件无法生成，所以先生成Path类文件)，如：ARouter$$Group$$app
        try {
            createGroupFile(groupLoadType,pathLoadType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    public class ARouter$$Path$$app implements ARouterLoadPath {
        @Override
        public Map<String, RouterBean> loadPath() {
            Map<String, RouterBean> pathMap = new HashMap<>();
            pathMap.put("/app/MainActivity", RouterBean.create(RouterBean.Type.ACTIVITY,
                    MainActivity.class,
                    "/app/MainActivity",
                    "app"));
            //同一个组内，可能有多个被注解的Activity
            return pathMap;
        }
    }
     /*

    /**
     * 生成路由组Group对应的详细Path，如：ARouter$$Path$$app
     * @param pathLoadType
     */
    private void createPathFile(TypeElement pathLoadType) throws IOException {
        if (EmptyUtils.isEmpty(tempPathMap)) return;
        //方法的返回值Map<String,RouterBean>
        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class)
        );
        //遍历分组，每一个分组创建一个路径类文件，如：ARouter$$Path$$app
        for (Map.Entry<String,List<RouterBean>> entry:tempPathMap.entrySet()){
            //方法体构造public Map<String,RouterBean> loadPath(){
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.PATH_METHOD_NAME)//方法名
                    .addAnnotation(Override.class)//重写注解
                    .addModifiers(Modifier.PUBLIC)//public修饰符
                    .returns(methodReturns);//方法返回值类型

            //不循环部分Map<String,RouterBean> pathMap = new HashMap<>();
            methodBuilder.addStatement("$T<$T,$T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    Constants.PATH_PARAMETER_NAME,
                    HashMap.class
            );
            //一个组内肯能有多个被注解的Activity /app/MainActivity,/app/xxxActivity
            List<RouterBean> pathList = entry.getValue();
            //方法内容的循环部分
            for (RouterBean bean : pathList) {
                /*
                 pathMap.put("/app/MainActivity", RouterBean.create(RouterBean.Type.ACTIVITY,
                                MainActivity.class,
                                "/app/MainActivity",
                                "app"));
                */
                methodBuilder.addStatement(
                        "$N.put($S,$T.create($T.$L,$T.class,$S,$S))",
                        Constants.PATH_PARAMETER_NAME,//pathMap
                        bean.getPath(),//"/app/MainActivity"
                        ClassName.get(RouterBean.class),
                        ClassName.get(RouterBean.Type.class),
                        bean.getType(),//枚举ACTIVITY
                        ClassName.get((TypeElement)bean.getElement()),//MainActivity.class
                        bean.getPath(),//"/app/MainActivity"
                        bean.getGroup());//"app"
            }
            //遍历过后，最后return pathMap
            methodBuilder.addStatement("return $N",Constants.PATH_PARAMETER_NAME);

            //生成类文件如：ARouter$$Path$$app
            String finalClassName = Constants.PATH_FILE_NAME + entry.getKey();
            messager.printMessage(Diagnostic.Kind.NOTE,"APT生成路由Path类文件为：" +
                    packageNameForAPT +"."+finalClassName);

            JavaFile.builder(packageNameForAPT,//包路径
                    TypeSpec.classBuilder(finalClassName)//类名
                    .addSuperinterface(ClassName.get(pathLoadType))//实现接口ARouterLoadPath
                    .addModifiers(Modifier.PUBLIC)//public 修饰符
                    .addMethod(methodBuilder.build())//方法的构建
                    .build()
            )
            .build()//返回javaFile
            .writeTo(filer);//类构建完成
            //别忘了，非常重要
            tempGroupMap.put(entry.getKey(),finalClassName);
        }

    }

    /**
    public class ARouter$$Group$$app implements ARouterLoadGroup {
        @Override
        public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
            Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
            groupMap.put("app", ARouter$$Path$$app.class);
            return groupMap;
        }
    }
    */


    /*
        public class ARouter$$Group$$app implements ARouterLoadGroup {
            @Override
            public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
                Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
                groupMap.put("app", ARouter$$Path$$app.class);
                return groupMap;
            }
        }
     */

    /**
     * 生成路由组Group文件，如：ARouter$$Group$$app
     * @param groupLoadType
     * @param pathLoadType
     */
    private void createGroupFile(TypeElement groupLoadType, TypeElement pathLoadType) throws IOException {
        //判断是否有必要生成类文件
        if (EmptyUtils.isEmpty(tempGroupMap) || EmptyUtils.isEmpty(tempPathMap)) return;

        //方法返回值类型Map<String, Class<? extends ARouterLoadPath>>
        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),//Map
                ClassName.get(String.class),//Map<String,
                //第二个参数：Class<? extends ARouterLoadPath>
                //?是ARouterLoadPath接口实现类
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType)))
        );

        // 方法配置：public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.GROUP_METHOD_NAME) // 方法名
                .addAnnotation(Override.class) // 重写注解
                .addModifiers(Modifier.PUBLIC) // public修饰符
                .returns(methodReturns); // 方法返回值

        // 遍历之前：Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
        methodBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))),
                Constants.GROUP_PARAMETER_NAME,
                HashMap.class);

        // 方法内容配置
        for (Map.Entry<String, String> entry : tempGroupMap.entrySet()) {
            // 类似String.format("hello %s net163 %d", "net", 163)通配符
            // groupMap.put("app", ARouter$$Path$$app.class);
            methodBuilder.addStatement("$N.put($S, $T.class)",
                    Constants.GROUP_PARAMETER_NAME, // groupMap.put
                    entry.getKey(),
                    // 类文件在指定包名下
                    ClassName.get(packageNameForAPT, entry.getValue()));
        }

        // 遍历之后：return groupMap;
        methodBuilder.addStatement("return $N", Constants.GROUP_PARAMETER_NAME);

        // 最终生成的类文件名
        String finalClassName = Constants.GROUP_FILE_NAME + moduleName;
        messager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由组Group类文件：" +
                packageNameForAPT + "." + finalClassName);

        // 生成类文件：ARouter$$Group$$app
        JavaFile.builder(packageNameForAPT, // 包名
                TypeSpec.classBuilder(finalClassName) // 类名
                        .addSuperinterface(ClassName.get(groupLoadType)) // 实现ARouterLoadGroup接口
                        .addModifiers(Modifier.PUBLIC) // public修饰符
                        .addMethod(methodBuilder.build()) // 方法的构建（方法参数 + 方法体）
                        .build()) // 类构建完成
                .build() // JavaFile构建完成
                .writeTo(filer); // 文件生成器开始生成类文件
    }

    /**
     * 赋值临时map存储，用来存放路由组Group对应的详细Path类对象，生成路由路径类文件时遍历
     *
     * @param bean 路由详细信息，最终实体封装类
     */
    private void valueOfPathMap(RouterBean bean) {
        if (checkRouterPath(bean)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean >>> " + bean.toString());
            //开始赋值
            List<RouterBean> routerBeans = tempPathMap.get(bean.getGroup());
            //如果从Map中找不到key
            if (EmptyUtils.isEmpty(routerBeans)) {
                routerBeans = new ArrayList<>();
                routerBeans.add(bean);
                tempPathMap.put(bean.getGroup(), routerBeans);
            } else {//找到了key，直接加入临时集合
                /*for (RouterBean routerBean : routerBeans) {
                    if (!bean.getPath().equalsIgnoreCase(routerBean.getPath())) {
                        routerBeans.add(bean);
                    }
                }*/
                routerBeans.add(bean);
            }
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范，如/app/MainActivity");
        }

    }

    /**
     * 检验@ARouter注解的值，如果group未填写就从必填项path中截取数据
     */
    private boolean checkRouterPath(RouterBean bean) {
        String group = bean.getGroup();
        String path = bean.getPath();
        //@ARouter注解的path值，必须要以/开头，模仿阿里ARouter路由架构
        if (EmptyUtils.isEmpty(path) || !path.startsWith("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范，如/app/MainActivity");
            return false;
        }

        //比如开发者代码为：path = "/MainActivity"
        if (path.lastIndexOf("/") == 0) {//排除只有一条/的情况
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范，如/app/MainActivity");
            return false;
        }
        int index = path.lastIndexOf("/");
        messager.printMessage(Diagnostic.Kind.NOTE, "/最后出现的index为 >>> " + index);
        if (path.substring(1,index).contains("/"))  {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范，如/app/MainActivity, /数量超过2");
            return false;//排除/数量超过2 比如"/app/MainActivity/"
        }
        //从第一个/到第二个/中间截取出组名group
        //substring(a,b) 从索引a到b截取字符串，左闭右开
        //path.indexOf("/",1),path从索引1开始，第一次出现/的索引，在此处为2
        String finalGroup = path.substring(1,path.indexOf("/",1));
        //比如path = ""
        if (finalGroup.length() == 0) {
            //排除两条//相连的情况比如："//MainActivity"
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范，如/app/MainActivity, //M" );
            return false;
        }

        //排除group乱写的情况比如："/dsf/MainActivity"
        if (!EmptyUtils.isEmpty(group) && !group.equals(moduleName)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范，如/app/MainActivity,group组名不规范");
            return false;
        }
        return true;
    }
}
