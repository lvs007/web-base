/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc.common;

import java.util.List;

public class Nodes extends Node {

    private Node left;
    private PathType pathType;
    private Node right;

    public Nodes(Node left, PathType pathType, Node right) {
        this.left = left;
        this.pathType = pathType;
        this.right = right;
    }

    @Override
    public void toSqlString(String alias, StringBuilder sb, List<Object> params) {
        sb.append("(");
        left.toSqlString(alias, sb, params);
        sb.append(" ").append(pathType.name()).append(" ");
        right.toSqlString(alias, sb, params);
        sb.append(")");
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public PathType getPathType() {
        return pathType;
    }

    public void setPathType(PathType pathType) {
        this.pathType = pathType;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

}
