// File generated by the BNF Converter (bnfc 2.9.4).

package lama.Absyn;

public abstract class Type implements java.io.Serializable {
  public abstract <R,A> R accept(Type.Visitor<R,A> v, A arg);
  public interface Visitor <R,A> {
    public R visit(lama.Absyn.TInt p, A arg);
    public R visit(lama.Absyn.TDouble p, A arg);
    public R visit(lama.Absyn.TBool p, A arg);
    public R visit(lama.Absyn.TArray p, A arg);

  }

}
