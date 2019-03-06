package com.liang.flow.config.data.service;

import com.alibaba.fastjson.JSON;
import com.liang.flow.config.ConfigService.InterfaceQps;
import com.liang.flow.config.ControllerObject;
import com.liang.flow.config.FlowConfig;
import com.liang.flow.config.data.dao.ForbiddenDao;
import com.liang.flow.config.data.entity.Forbidden;
import com.liang.flow.dto.ForbiddenVo;
import com.liang.flow.interceptor.FlowController;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangzhiyan on 2018/3/21.
 */
@Service
public class ForbiddenService {

    @Autowired
    private ForbiddenDao forbiddenDao;

    @PostConstruct
    private void loadConfig() {
        List<Forbidden> forbiddenList = forbiddenDao.listAll();
        for (Forbidden forbidden : forbiddenList) {
            System.out.println(forbidden);
            ControllerObject controllerObject = ControllerObject.buildControllerObject(forbidden);
            InterfaceQps interfaceQps = InterfaceQps.buildInterfaceQps(forbidden);
            FlowConfig.changeListener(controllerObject);
            FlowController.changeListener(interfaceQps);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insert(ForbiddenVo forbiddenVo) {
        return forbiddenDao.insert(buildForbidden(forbiddenVo));
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean update(ForbiddenVo forbiddenVo) {
        return forbiddenDao.update(buildForbidden(forbiddenVo));
    }

    public List<ForbiddenVo> list() {
        List<Forbidden> forbiddenList = forbiddenDao.listAll();
        List<ForbiddenVo> forbiddenVoList = new ArrayList<>();
        for (Forbidden forbidden : forbiddenList) {
            forbiddenVoList.add(renderToForbiddenVo(forbidden));
        }
        return forbiddenVoList;
    }

    public ForbiddenVo findById(long id) {
        Forbidden forbidden = forbiddenDao.findById(id);
        return renderToForbiddenVo(forbidden);
    }

    private Forbidden buildForbidden(ForbiddenVo forbiddenVo) {
        Forbidden forbidden = new Forbidden();
        BeanUtils.copyProperties(forbiddenVo, forbidden);
        System.out.println("Forbidden:" + JSON.toJSONString(forbidden));
        return forbidden;
    }

    private ForbiddenVo renderToForbiddenVo(Forbidden forbidden) {
        ForbiddenVo forbiddenVo = new ForbiddenVo();
        BeanUtils.copyProperties(forbidden, forbiddenVo);
        return forbiddenVo;
    }
}
