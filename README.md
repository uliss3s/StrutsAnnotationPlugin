# Struts Annotation Plugin

Struts 1.3.X Plugin that adds annotation support for mapping Actions and POJOs (as DynaActionForm).

# Info

It depends on Spring "org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider" class

# Usage

Add this in your project main struts configuration file:
```xml
<plug-in className="com.github.uliss3s.strutsannotationplugin.Plugin">
    <set-property property="actionsPackageLocation" value="my.app.web.actions"/>
    <set-property property="formsPackageLocation" value="my.app.domain"/>
</plug-in>
```

Basic Action Configuration:

```java
import com.github.uliss3s.strutsannotationplugin.annotations.Action;
import com.github.uliss3s.strutsannotationplugin.annotations.Forward;
import com.github.uliss3s.strutsannotationplugin.annotations.ActionScope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Action(path = "/my/url/action", scope = ActionScope.REQUEST, forwards = {
	@Forward(name = "default", path = "/my/url/action")
})
public class MyAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
        HttpServletRequest request, HttpServletResponse response) throws Exception {

    	...

    	return mapping.findForward("default");
    }

    ...

}
```