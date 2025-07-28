package io.silvicky.novel.compiler.parser.expression;

public class Rotator
{
    public static <T extends LTRExpression> T rotateLeft(T root)
    {
        T currentRoot=root;
        T right;
        while(true)
        {
            try
            {
                right = (T) (currentRoot.right);
            }
            catch(ClassCastException e)
            {
                return currentRoot;
            }
            if(right==null)
            {
                if(currentRoot.left.getClass().equals(root.getClass()))return (T) currentRoot.left;
                else return currentRoot;
            }
            currentRoot.right=right.left;
            right.left=currentRoot;
            currentRoot=right;
        }
    }
}
