package io.silvicky.novel.novel.output;

public class TreeNode<T> {
    private final T content;
    private final TreeNode<T> parent;
    private TreeNode<T> firstChild;
    private TreeNode<T> lastChild;
    private TreeNode<T> nextSibling;

    public TreeNode(T content, TreeNode<T> parent) {
        this.content = content;
        this.parent = parent;
    }

    TreeNode<T> addChild(T content) {
        TreeNode<T> child = new TreeNode<>(content, this);
        if(firstChild==null) {
            firstChild = child;
            lastChild = child;
            return child;
        }
        lastChild.nextSibling = child;
        lastChild = child;
        return child;
    }

    T content() {return content;}

    TreeNode<T> getNext() {
        TreeNode<T> cur = this;
        if (cur.firstChild != null) {
            return cur.firstChild;
        }
        while(cur != null && cur.nextSibling == null) {
            cur=cur.parent;
        }
        if(cur==null) {
            return null;
        }
        return cur.nextSibling;
    }
}
