package liang.flow.config.data.listener;

import liang.flow.dto.ForbiddenVo;
import liang.mvc.event.BaseEvent;

/**
 * Created by liangzhiyan on 2018/3/22.
 */
public class ForbiddenEvent extends BaseEvent {

    private Type type;
    private ForbiddenVo forbiddenVo;

    public ForbiddenEvent(ForbiddenVo forbiddenVo) {
        this.forbiddenVo = forbiddenVo;
    }

    public ForbiddenEvent(Type type, ForbiddenVo forbiddenVo) {
        this.type = type;
        this.forbiddenVo = forbiddenVo;
    }

    public Type getType() {
        return type;
    }

    public ForbiddenEvent setType(Type type) {
        this.type = type;
        return this;
    }

    public ForbiddenVo getForbiddenVo() {
        return forbiddenVo;
    }

    public ForbiddenEvent setForbiddenVo(ForbiddenVo forbiddenVo) {
        this.forbiddenVo = forbiddenVo;
        return this;
    }

    public static enum Type {
        CREATE, UPDATE, DELETE
    }
}
