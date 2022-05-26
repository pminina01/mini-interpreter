// File generated by the BNF Converter (bnfc 2.9.4).

package lama.Absyn;

public class EBool  extends Exp {
  public final Bool bool_;
  public EBool(Bool p1) { bool_ = p1; }

  public <R,A> R accept(lama.Absyn.Exp.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(java.lang.Object o) {
    if (this == o) return true;
    if (o instanceof lama.Absyn.EBool) {
      lama.Absyn.EBool x = (lama.Absyn.EBool)o;
      return this.bool_.equals(x.bool_);
    }
    return false;
  }

  public int hashCode() {
    return this.bool_.hashCode();
  }


}