package com.zhou;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.IdentityLink;
import org.junit.Test;

public class Main {

	public static void main(String[] args) {
		new Main().test3();
	}

	/**
	 * 流程权限设置与查询
	 */
	@Test
	public void test1() {
		// 加载默认配置文件
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();

		// 身份服务
		IdentityService identityService = engine.getIdentityService();
		// 存储服务
		RepositoryService repositoryService = engine.getRepositoryService();

		// 创建用户1
		User user1 = identityService.newUser("");
		user1.setId(null);
		user1.setLastName("user1");
		identityService.saveUser(user1);

		// 创建用户2
		User user2 = identityService.newUser("");
		user2.setId(null);
		user2.setLastName("user2");
		identityService.saveUser(user2);

		// 创建用户3
		User user3 = identityService.newUser("");
		user3.setId(null);
		user3.setLastName("user3");
		identityService.saveUser(user3);

		// 部署流程1
		DeploymentBuilder builder1 = repositoryService.createDeployment();
		builder1.addClasspathResource("process1.bpmn");
		Deployment deploy1 = builder1.name("deploy1").deploy();

		// 部署流程2
		DeploymentBuilder builder2 = repositoryService.createDeployment();
		builder2.addClasspathResource("process2.bpmn");
		Deployment deploy2 = builder2.name("deploy2").deploy();

		ProcessDefinition processDefinition1 = repositoryService.createProcessDefinitionQuery()
				.deploymentId(deploy1.getId()).singleResult();
		ProcessDefinition processDefinition2 = repositoryService.createProcessDefinitionQuery()
				.deploymentId(deploy2.getId()).singleResult();

		System.out.println(
				String.format("流程定义1: id = %s, name = %s", processDefinition1.getId(), processDefinition1.getName()));
		System.out.println(
				String.format("流程定义2: id = %s, name = %s", processDefinition2.getId(), processDefinition2.getName()));

		// 把流程1权限分配给用户1和用户2
		repositoryService.addCandidateStarterUser(processDefinition1.getId(), user1.getId());
		repositoryService.addCandidateStarterUser(processDefinition1.getId(), user2.getId());

		// 把流程2权限分配给用户2和用户3
		repositoryService.addCandidateStarterUser(processDefinition2.getId(), user2.getId());
		repositoryService.addCandidateStarterUser(processDefinition2.getId(), user3.getId());

		// 分别查询用户1,2,3拥有权限的流程
		List<ProcessDefinition> user1processDefinitions = repositoryService.createProcessDefinitionQuery()
				.startableByUser(user1.getId()).list();
		List<ProcessDefinition> user2processDefinitions = repositoryService.createProcessDefinitionQuery()
				.startableByUser(user2.getId()).list();
		List<ProcessDefinition> user3processDefinitions = repositoryService.createProcessDefinitionQuery()
				.startableByUser(user3.getId()).list();

		for (ProcessDefinition pd : user1processDefinitions) {
			System.out.println(String.format("user1 : %s", pd.getName()));
		}
		for (ProcessDefinition pd : user2processDefinitions) {
			System.out.println(String.format("user2 : %s", pd.getName()));
		}
		for (ProcessDefinition pd : user3processDefinitions) {
			System.out.println(String.format("user3 : %s", pd.getName()));
		}

		// 分别查询流程1, 2分配的用户
		List<User> processDefinition1Users = identityService.createUserQuery()
				.potentialStarter(processDefinition1.getId()).list();
		List<User> processDefinition2Users = identityService.createUserQuery()
				.potentialStarter(processDefinition2.getId()).list();
		for (User user : processDefinition1Users) {
			System.out.println(String.format("procDef1 : %s", user.getLastName()));
		}
		for (User user : processDefinition2Users) {
			System.out.println(String.format("procDef2 : %s", user.getLastName()));
		}
	}

	/**
	 * 通过用户组分配流程权限给用户及查询
	 */
	@Test
	public void test2() {
		// 通过默认配置文件加载引擎
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		// 身份服务
		IdentityService identityService = engine.getIdentityService();
		// 存储服务
		RepositoryService repositoryService = engine.getRepositoryService();

		// 添加用户组1
		Group group1 = identityService.newGroup("");
		group1.setId(null);
		group1.setName("group1");
		identityService.saveGroup(group1);

		// 添加用户组2
		Group group2 = identityService.newGroup("");
		group2.setId(null);
		group2.setName("group2");
		identityService.saveGroup(group2);

		System.out.println("用户组:");
		System.out.println(String.format("group1.id = %s, group1.name = %s", group1.getId(), group1.getName()));
		System.out.println(String.format("group2.id = %s, group2.name = %s", group2.getId(), group2.getName()));

		// 添加用户1
		User user1 = identityService.newUser("");
		user1.setId(null);
		user1.setLastName("user1");
		identityService.saveUser(user1);

		// 添加用户2
		User user2 = identityService.newUser("");
		user2.setId(null);
		user2.setLastName("user2");
		identityService.saveUser(user2);

		// 添加用户3
		User user3 = identityService.newUser("");
		user3.setId(null);
		user3.setLastName("user3");
		identityService.saveUser(user3);

		System.out.println("用户:");
		System.out.println(String.format("user1.id = %s, user1.name = %s", user1.getId(), user1.getLastName()));
		System.out.println(String.format("user2.id = %s, user2.name = %s", user2.getId(), user2.getLastName()));
		System.out.println(String.format("user3.id = %s, user3.name = %s", user3.getId(), user3.getLastName()));

		// 部署两个流程
		Deployment deploy1 = repositoryService.createDeployment().addClasspathResource("process1.bpmn").name("deploy1")
				.deploy();
		Deployment deploy2 = repositoryService.createDeployment().addClasspathResource("process2.bpmn").name("deploy2")
				.deploy();

		System.out.println("部署:");
		System.out.println(String.format("deploy1.id = %s, deploy1.name = %s", deploy1.getId(), deploy1.getName()));
		System.out.println(String.format("deploy2.id = %s, deploy2.name = %s", deploy2.getId(), deploy2.getName()));

		// 把用户1,2放到用户组1
		identityService.createMembership(user1.getId(), group1.getId());
		identityService.createMembership(user2.getId(), group1.getId());

		// 把用户2,3放到用户组2
		identityService.createMembership(user2.getId(), group2.getId());
		identityService.createMembership(user3.getId(), group2.getId());

		// 把流程1与用户组1关联
		ProcessDefinition procDef1 = repositoryService.createProcessDefinitionQuery().deploymentId(deploy1.getId())
				.singleResult();
		repositoryService.addCandidateStarterGroup(procDef1.getId(), group1.getId());

		// 把流程2与用户组2关联
		ProcessDefinition procDef2 = repositoryService.createProcessDefinitionQuery().deploymentId(deploy2.getId())
				.singleResult();
		repositoryService.addCandidateStarterGroup(procDef2.getId(), group2.getId());

		// =================开始查询===================
		// 1.查询用户所在用户组
		System.out.println("1.查询用户所在用户组");
		List<Group> user1Groups = identityService.createGroupQuery().groupMember(user1.getId()).list();
		List<Group> user2Groups = identityService.createGroupQuery().groupMember(user2.getId()).list();
		List<Group> user3Groups = identityService.createGroupQuery().groupMember(user3.getId()).list();

		for (Group g : user1Groups) {
			System.out.println(String.format("user1--%s", g.getName()));
		}
		
		for (Group g : user2Groups) {
			System.out.println(String.format("user2--%s", g.getName()));
		}
		
		for (Group g : user3Groups) {
			System.out.println(String.format("user3--%s", g.getName()));
		}
		
		// 2.查询用户组里包含的用户
		System.out.println("2.查询用户组里包含的用户");
		List<User> group1Users = identityService.createUserQuery().memberOfGroup(group1.getId()).list();
		List<User> group2Users = identityService.createUserQuery().memberOfGroup(group2.getId()).list();
		
		for(User u : group1Users) {
			System.out.println(String.format("group1--%s", u.getLastName()));
		}
		
		
		for(User u : group2Users) {
			System.out.println(String.format("group2--%s", u.getLastName()));
		}
		
		// 3.查询用户组拥有权限的流程
		System.out.println("3.查询用户组拥有权限的流程");
		List<ProcessDefinition> procDefs1 = repositoryService.createProcessDefinitionQuery().startableByUser(user1.getId()).list();
		List<ProcessDefinition> procDefs2 = repositoryService.createProcessDefinitionQuery().startableByUser(user2.getId()).list();
		List<ProcessDefinition> procDefs3 = repositoryService.createProcessDefinitionQuery().startableByUser(user3.getId()).list();
		
		for(ProcessDefinition pro : procDefs1) {
			System.out.println(String.format("user1--%s", pro.getName()));			
		}
		
		for(ProcessDefinition pro : procDefs2) {
			System.out.println(String.format("user2--%s", pro.getName()));			
		}	
		
		for(ProcessDefinition pro : procDefs3) {
			System.out.println(String.format("user3--%s", pro.getName()));			
		}	
		
		// 4.查询流程所分配的用户组
		System.out.println("4.查询流程所分配的用户组");
		List<Group> groups1 = identityService.createGroupQuery().potentialStarter(procDef2.getId()).list();
		List<Group> groups2 = identityService.createGroupQuery().potentialStarter(procDef2.getId()).list();
		
		for(Group g : groups1) {
			System.out.println(String.format("deploy1--%s", g.getName()));
		}
		
		for(Group g : groups2) {
			System.out.println(String.format("deploy2--%s", g.getName()));
		}
	}

	/**
	 * 通过IdentityLink查询
	 */
	@Test
	public void test3() {
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		IdentityService identityService = engine.getIdentityService();
		RepositoryService repositoryService = engine.getRepositoryService();
		
		// 分别添加3个用户
		User user1 = identityService.newUser("");
		user1.setId(null);
		user1.setLastName("user1");
		identityService.saveUser(user1);
		
		User user2 = identityService.newUser("");
		user2.setId(null);
		user2.setLastName("user2");
		identityService.saveUser(user2);
		
		User user3 = identityService.newUser("");
		user3.setId(null);
		user3.setLastName("user3");	
		identityService.saveUser(user3);
		
		Deployment deploy1 = repositoryService.createDeployment().addClasspathResource("process1.bpmn").name("deploy1").deploy();
		Deployment deploy2 = repositoryService.createDeployment().addClasspathResource("process2.bpmn").name("deploy2").deploy();
		
		ProcessDefinition procDef1 = repositoryService.createProcessDefinitionQuery().deploymentId(deploy1.getId()).singleResult();
		ProcessDefinition procDef2 = repositoryService.createProcessDefinitionQuery().deploymentId(deploy2.getId()).singleResult();
		
		repositoryService.addCandidateStarterUser(procDef1.getId(), user1.getId());
		repositoryService.addCandidateStarterUser(procDef1.getId(), user2.getId());
		repositoryService.addCandidateStarterUser(procDef2.getId(), user2.getId());
		repositoryService.addCandidateStarterUser(procDef2.getId(), user3.getId());
		
		// ===================开始查询======================
		List<IdentityLink> procDef1Users = repositoryService.getIdentityLinksForProcessDefinition(procDef1.getId());
		List<IdentityLink> procDef2Users = repositoryService.getIdentityLinksForProcessDefinition(procDef2.getId());
		
		for(IdentityLink identityLink : procDef1Users) {
			System.out.println(String.format("procDef.id = %s, user.id = %s", identityLink.getProcessDefinitionId(), identityLink.getUserId()));
		}
		
		for(IdentityLink identityLink : procDef2Users) {
			System.out.println(String.format("procDef.id = %s, user.id = %s", identityLink.getProcessDefinitionId(), identityLink.getUserId()));
		}
		
		List<User> users1 = identityService.createUserQuery().potentialStarter(procDef1.getId()).list();
		List<User> users2 = identityService.createUserQuery().potentialStarter(procDef2.getId()).list();
		
		for(User user : users1) {
			System.out.println(String.format("procDef1.name = %s, user.name = %s", procDef1.getName(), user.getLastName()));
		}
		
		for(User user : users2) {
			System.out.println(String.format("procDef1.name = %s, user.name = %s", procDef2.getName(), user.getLastName()));
		}
	}
}
