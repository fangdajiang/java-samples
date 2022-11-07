package com.xxdai.starter.experiment.bpmn;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;

import java.util.List;

/**
 * 参考：《Activiti 就是这么简单》 https://juejin.im/post/5aafa3eef265da23784015b9
 */
public class BasicTest {

    @Test
    public void deployOne() {
//        默认会加载类路径下的 activiti.cfg.xml
//        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessEngine processEngine = getProcessEngine();
        System.out.println("processEngine:" + processEngine);
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()//创建一个部署的构建器
                .addClasspathResource("FirstActiviti.bpmn")//从类路径中添加资源,一次只能添加一个资源
                .name("请求单流程2")//设置部署的名称
                .category("办公类别2")//设置部署的类别
                .deploy();

        System.out.println("部署的id:" + deploy.getId());
        System.out.println("部署的名称:" + deploy.getName());
    }

    //查询任务
    @Test
    public void queryTask(){
        //任务的办理人
        String assignee = "Foo";
        //取得任务服务
        TaskService taskService = getProcessEngine().getTaskService();
        //创建一个任务查询对象
        TaskQuery taskQuery = taskService.createTaskQuery();
        //办理人的任务列表
        List<Task> list = taskQuery.taskAssignee(assignee)//指定办理人
                .list();
        System.out.println("list:" + list);
        //遍历任务列表
        if(list != null && list.size() > 0){
            for(Task task:list){
                System.out.println("任务的办理人：" + task.getAssignee());
                System.out.println("任务的id：" + task.getId());
                System.out.println("任务的名称：" + task.getName());
                System.out.println();
            }
        }
    }

    //完成任务
    @Test
    public void compileTask(){
        String taskId="7505";
        TaskService taskService = getProcessEngine().getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> list = taskQuery.taskAssignee("Foo")//指定办理人
                .list();
        System.out.println("list:" + list);

        taskService.complete(taskId);
        System.out.println("当前任务执行完毕");
    }

    @Test
    public void startProcess(){
        //指定执行我们刚才部署的工作流程
        String processDefiKey="myProcess_1";
        //取运行时服务
        RuntimeService runtimeService = getProcessEngine().getRuntimeService();
        //取得流程实例
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(processDefiKey);//通过流程定义的key 来执行流程
        System.out.println("流程实例id:" + pi.getId());//流程实例id
        System.out.println("流程定义id:" + pi.getProcessDefinitionId());//输出流程定义的id
    }

    private ProcessEngine getProcessEngine() {
        return initProcessEngineConfiguration().buildProcessEngine();
    }

    private ProcessEngineConfiguration initProcessEngineConfiguration() {
        /**1.通过代码形式创建
         *  - 取得ProcessEngineConfiguration对象
         *  - 设置数据库连接属性
         *  - 设置创建表的策略 （当没有表时，自动创建表）
         *  - 通过ProcessEngineConfiguration对象创建 ProcessEngine 对象*/

        //取得ProcessEngineConfiguration对象
        ProcessEngineConfiguration engineConfiguration = ProcessEngineConfiguration.
                createStandaloneProcessEngineConfiguration();
        //设置数据库连接属性
        engineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
        engineConfiguration.setJdbcUrl("jdbc:mysql://192.168.33.46:3306/activitiDB?createDatabaseIfNotExist=true"
                + "&useUnicode=true&characterEncoding=utf8");
        engineConfiguration.setJdbcUsername("fangdajiang");
        engineConfiguration.setJdbcPassword("");


        // 设置创建表的策略 （当没有表时，自动创建表）
        //		  public static final java.lang.String DB_SCHEMA_UPDATE_FALSE = "false";//不会自动创建表，没有表，则抛异常
        //		  public static final java.lang.String DB_SCHEMA_UPDATE_CREATE_DROP = "create-drop";//先删除，再创建表
        //		  public static final java.lang.String DB_SCHEMA_UPDATE_TRUE = "true";//假如没有表，则自动创建
        engineConfiguration.setDatabaseSchemaUpdate("true");
        //通过ProcessEngineConfiguration对象创建 ProcessEngine 对象

        return engineConfiguration;
    }

    @Test
    public void createActivitiEngine(){
        ProcessEngine processEngine = getProcessEngine();
        System.out.println("流程引擎创建成功：" + processEngine);
    }

}
