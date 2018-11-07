/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.email.mail;

import java.util.List;

/**
 * 抽象的email接口
 *
 * @author
 */
public interface Email {

    public List<String> getToList();

    public List<String> getCcList();

    public List<String> getBccList();

    public String getSubject();

    public String getContent();

    public String getFromName();
}
