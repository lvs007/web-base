package liang.flow.action;

import liang.flow.config.data.listener.ForbiddenEvent;
import liang.flow.config.data.service.ForbiddenService;
import liang.flow.dto.ForbiddenVo;
import liang.mvc.annotation.PcLogin;
import liang.mvc.commons.SpringContextHolder;
import liang.mvc.event.EventBusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

import static liang.flow.config.data.listener.ForbiddenEvent.Type.CREATE;
import static liang.flow.config.data.listener.ForbiddenEvent.Type.UPDATE;

/**
 * Created by liangzhiyan on 2018/3/21.
 */
@Controller
public class ForbiddenController {

    @Autowired
    private ForbiddenService forbiddenService;

    @Autowired
    private EventBusService eventBusService;

    @PcLogin
    public String insertPage() {
        return "insert";
    }

    @PcLogin
    public String listPage(ModelMap modelMap) {
        modelMap.put("result", forbiddenService.list());
        return "list";
    }

    @PcLogin
    public String updatePage(long id, ModelMap modelMap) {
        modelMap.put("forbidden", forbiddenService.findById(id));
        return "update";
    }

    @PcLogin
    public String insert(ForbiddenVo forbiddenVo) throws IOException {
        forbiddenService.insert(forbiddenVo);
        eventBusService.postEvent(new ForbiddenEvent(CREATE, forbiddenVo));
        SpringContextHolder.getResponse().sendRedirect("/v1/forbidden/list-page");
        return "list";
    }

    @PcLogin
    public String update(ForbiddenVo forbiddenVo) throws IOException {
        forbiddenService.update(forbiddenVo);
        eventBusService.postEvent(new ForbiddenEvent(UPDATE, forbiddenVo));
        SpringContextHolder.getResponse().sendRedirect("/v1/forbidden/list-page");
        return "list";
    }

    @PcLogin
    @ResponseBody
    public Object list() {
        return forbiddenService.list();
    }

}
