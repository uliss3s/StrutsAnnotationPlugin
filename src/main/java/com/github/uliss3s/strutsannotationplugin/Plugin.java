/**
 * This Struts 1.3.X Plugin Class implements annotation support for mapping actions and POJO's as a DynaActionForm
 * It depends on Spring "org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider" class
 */

package com.github.uliss3s.strutsannotationplugin;

import com.github.uliss3s.strutsannotationplugin.annotations.Action;
import com.github.uliss3s.strutsannotationplugin.annotations.ActionForm;
import com.github.uliss3s.strutsannotationplugin.annotations.FormProperty;
import com.github.uliss3s.strutsannotationplugin.annotations.Forward;
import com.github.uliss3s.strutsannotationplugin.parameters.MappingType;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.config.ModuleConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.servlet.ServletException;
import java.lang.reflect.Field;
import java.util.Set;

public class Plugin implements PlugIn {
	
	private String actionsPackageLocation;
	private String formsPackageLocation;

	@Override
	public void destroy() {
		
	}

	@Override
	public void init(ActionServlet servlet, ModuleConfig config) throws ServletException {
		
		ClassPathScanningCandidateComponentProvider classPathScanning = new ClassPathScanningCandidateComponentProvider(false);
		classPathScanning.addIncludeFilter(new AnnotationTypeFilter(Action.class));
		
		Set<BeanDefinition> candidates = classPathScanning.findCandidateComponents(actionsPackageLocation);
		
		configureActions(candidates, config);
		
		classPathScanning = new ClassPathScanningCandidateComponentProvider(false);
		classPathScanning.addIncludeFilter(new AnnotationTypeFilter(ActionForm.class));
		
		candidates = classPathScanning.findCandidateComponents(formsPackageLocation);
		
		configureForms(candidates, config);
	}
	
	private void configureActions(Set<BeanDefinition> candidates, ModuleConfig config) throws ServletException {
		for (BeanDefinition beanDefinition : candidates) {
			
			try {
				Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
				Action strutsAction = clazz.getAnnotation(Action.class);
				
				ActionMapping actionMapping = new ActionMapping();
				if (!strutsAction.name().isEmpty()) {
					actionMapping.setName(strutsAction.name());
				}
				actionMapping.setScope(strutsAction.scope().toString());
				if (!strutsAction.parameter().isEmpty()) {
					actionMapping.setParameter(strutsAction.parameter());
				}
				actionMapping.setType(beanDefinition.getBeanClassName());
				actionMapping.setPath(strutsAction.path());
				
				Forward[] forwards = strutsAction.forwards();
				
				for (Forward forward : forwards) {
					ActionForward actionForward = new ActionForward(forward.name(), forward.path(), forward.redirect());
					actionMapping.addForwardConfig(actionForward);
				}
				
				config.addActionConfig(actionMapping);
				
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
	}
	
	private void configureForms(Set<BeanDefinition> candidates, ModuleConfig config) throws ServletException {
		for (BeanDefinition beanDefinition : candidates) {
			
			try {
				Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
				ActionForm actionForm = clazz.getAnnotation(ActionForm.class);
				
				FormBeanConfig formBeanConfig = new FormBeanConfig();
				formBeanConfig.setName(actionForm.name());
				formBeanConfig.setType("org.apache.struts.action.DynaActionForm");
				
				Field[] fields = clazz.getDeclaredFields();
				
					for (Field field : fields) {
						if (actionForm.mappingType().equals(MappingType.ALL_ATRIBUTES)) {
							formBeanConfig.setProperty(field.getName(), field.getType().getName());
						} else if (actionForm.mappingType().equals(MappingType.BY_INCLUSION)) {
							FormProperty formProperty = field.getAnnotation(FormProperty.class);
							if (formProperty != null) {
								formBeanConfig.setProperty(field.getName(), field.getType().getName());
							}
						}
					}
				
				config.addFormBeanConfig(formBeanConfig);
				
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
	}

	public String getActionsPackageLocation() {
		return actionsPackageLocation;
	}

	public void setActionsPackageLocation(String actionsPackageLocation) {
		this.actionsPackageLocation = actionsPackageLocation;
	}

	public String getFormsPackageLocation() {
		return formsPackageLocation;
	}

	public void setFormsPackageLocation(String formsPackageLocation) {
		this.formsPackageLocation = formsPackageLocation;
	}
}
