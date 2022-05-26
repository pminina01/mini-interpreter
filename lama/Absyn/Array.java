// File generated by the BNF Converter (bnfc 2.9.4).

package lama.Absyn;

public class Array  extends Exp {
  public final Type type_;
  public final ListExp listexp_;
  public Array(Type p1, ListExp p2) { type_ = p1; listexp_ = p2; }

  public <R,A> R accept(lama.Absyn.Exp.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(java.lang.Object o) {
    if (this == o) return true;
    if (o instanceof lama.Absyn.Array) {
      lama.Absyn.Array x = (lama.Absyn.Array)o;
      return this.type_.equals(x.type_) && this.listexp_.equals(x.listexp_);
    }
    return false;
  }

  public int hashCode() {
    return 37*(this.type_.hashCode())+this.listexp_.hashCode();
  }


}