// File generated by the BNF Converter (bnfc 2.9.4).

package lama.Absyn;

public class IsEmpty  extends Exp {
  public final Exp exp_;
  public IsEmpty(Exp p1) { exp_ = p1; }

  public <R,A> R accept(lama.Absyn.Exp.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(java.lang.Object o) {
    if (this == o) return true;
    if (o instanceof lama.Absyn.IsEmpty) {
      lama.Absyn.IsEmpty x = (lama.Absyn.IsEmpty)o;
      return this.exp_.equals(x.exp_);
    }
    return false;
  }

  public int hashCode() {
    return this.exp_.hashCode();
  }


}