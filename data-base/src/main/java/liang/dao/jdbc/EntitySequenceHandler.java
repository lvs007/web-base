/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc;

/**
 * 实体序列的处理器，实现此接口的类必须响应处理相应的实体，
 * 这样的话，就可以直接在数据库底层生成大量的对象，交给程序进行
 * 逻辑处理
 *
 * @author
 * @param <T>
 */
public interface EntitySequenceHandler<T> {

    public void handleEntity(T entity);
}
