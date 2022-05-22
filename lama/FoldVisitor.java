// File generated by the BNF Converter (bnfc 2.9.4).

package lama;

/** Fold Visitor */
public abstract class FoldVisitor<R,A> implements AllVisitor<R,A> {
    public abstract R leaf(A arg);
    public abstract R combine(R x, R y, A arg);

/* Program */
    public R visit(lama.Absyn.Prog p, A arg) {
      R r = leaf(arg);
      for (lama.Absyn.Stm x : p.liststm_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }

/* Stm */
    public R visit(lama.Absyn.SDecl p, A arg) {
      R r = leaf(arg);
      r = combine(p.type_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(lama.Absyn.SAss p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(lama.Absyn.SInit p, A arg) {
      R r = leaf(arg);
      r = combine(p.type_.accept(this, arg), r, arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(lama.Absyn.SBlock p, A arg) {
      R r = leaf(arg);
      for (lama.Absyn.Stm x : p.liststm_)
      {
        r = combine(x.accept(this, arg), r, arg);
      }
      return r;
    }
    public R visit(lama.Absyn.SPrint p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_.accept(this, arg), r, arg);
      return r;
    }

/* Exp */
    public R visit(lama.Absyn.EVar p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(lama.Absyn.EInt p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(lama.Absyn.EDouble p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(lama.Absyn.EBool p, A arg) {
      R r = leaf(arg);
      r = combine(p.bool_.accept(this, arg), r, arg);
      return r;
    }
    public R visit(lama.Absyn.EPostIncr p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(lama.Absyn.EPostDecr p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(lama.Absyn.EPreIncr p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(lama.Absyn.EPreDecr p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(lama.Absyn.EMul p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(lama.Absyn.EDiv p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(lama.Absyn.EAdd p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(lama.Absyn.ESub p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(lama.Absyn.ELess p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(lama.Absyn.EGreater p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(lama.Absyn.ELEq p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(lama.Absyn.EGEq p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(lama.Absyn.EEq p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }
    public R visit(lama.Absyn.ENEq p, A arg) {
      R r = leaf(arg);
      r = combine(p.exp_1.accept(this, arg), r, arg);
      r = combine(p.exp_2.accept(this, arg), r, arg);
      return r;
    }

/* Bool */
    public R visit(lama.Absyn.Bool_true p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(lama.Absyn.Bool_false p, A arg) {
      R r = leaf(arg);
      return r;
    }

/* Type */
    public R visit(lama.Absyn.TInt p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(lama.Absyn.TDouble p, A arg) {
      R r = leaf(arg);
      return r;
    }
    public R visit(lama.Absyn.TBool p, A arg) {
      R r = leaf(arg);
      return r;
    }


}
