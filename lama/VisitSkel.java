// File generated by the BNF Converter (bnfc 2.9.4).

package lama;

/*** Visitor Design Pattern Skeleton. ***/

/* This implements the common visitor design pattern.
   Tests show it to be slightly less efficient than the
   instanceof method, but easier to use.
   Replace the R and A parameters with the desired return
   and context types.*/

public class VisitSkel
{
  public class ProgramVisitor<R,A> implements lama.Absyn.Program.Visitor<R,A>
  {
    public R visit(lama.Absyn.Prog p, A arg)
    { /* Code for Prog goes here */
      for (lama.Absyn.Stm x: p.liststm_) {
        x.accept(new StmVisitor<R,A>(), arg);
      }
      return null;
    }
  }
  public class StmVisitor<R,A> implements lama.Absyn.Stm.Visitor<R,A>
  {
    public R visit(lama.Absyn.SDecl p, A arg)
    { /* Code for SDecl goes here */
      p.type_.accept(new TypeVisitor<R,A>(), arg);
      //p.ident_;
      return null;
    }
    public R visit(lama.Absyn.SAss p, A arg)
    { /* Code for SAss goes here */
      //p.ident_;
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
    public R visit(lama.Absyn.SInit p, A arg)
    { /* Code for SInit goes here */
      p.type_.accept(new TypeVisitor<R,A>(), arg);
      //p.ident_;
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
    public R visit(lama.Absyn.SBlock p, A arg)
    { /* Code for SBlock goes here */
      for (lama.Absyn.Stm x: p.liststm_) {
        x.accept(new StmVisitor<R,A>(), arg);
      }
      return null;
    }
    public R visit(lama.Absyn.SPrint p, A arg)
    { /* Code for SPrint goes here */
      p.exp_.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
  }
  public class ExpVisitor<R,A> implements lama.Absyn.Exp.Visitor<R,A>
  {
    public R visit(lama.Absyn.EVar p, A arg)
    { /* Code for EVar goes here */
      //p.ident_;
      return null;
    }
    public R visit(lama.Absyn.EInt p, A arg)
    { /* Code for EInt goes here */
      //p.integer_;
      return null;
    }
    public R visit(lama.Absyn.EDouble p, A arg)
    { /* Code for EDouble goes here */
      //p.double_;
      return null;
    }
    public R visit(lama.Absyn.EBool p, A arg)
    { /* Code for EBool goes here */
      p.bool_.accept(new BoolVisitor<R,A>(), arg);
      return null;
    }
    public R visit(lama.Absyn.EPostIncr p, A arg)
    { /* Code for EPostIncr goes here */
      //p.ident_;
      return null;
    }
    public R visit(lama.Absyn.EPostDecr p, A arg)
    { /* Code for EPostDecr goes here */
      //p.ident_;
      return null;
    }
    public R visit(lama.Absyn.EPreIncr p, A arg)
    { /* Code for EPreIncr goes here */
      //p.ident_;
      return null;
    }
    public R visit(lama.Absyn.EPreDecr p, A arg)
    { /* Code for EPreDecr goes here */
      //p.ident_;
      return null;
    }
    public R visit(lama.Absyn.EMul p, A arg)
    { /* Code for EMul goes here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
    public R visit(lama.Absyn.EDiv p, A arg)
    { /* Code for EDiv goes here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
    public R visit(lama.Absyn.EAdd p, A arg)
    { /* Code for EAdd goes here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
    public R visit(lama.Absyn.ESub p, A arg)
    { /* Code for ESub goes here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
    public R visit(lama.Absyn.ELess p, A arg)
    { /* Code for ELess goes here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
    public R visit(lama.Absyn.EGreater p, A arg)
    { /* Code for EGreater goes here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
    public R visit(lama.Absyn.ELEq p, A arg)
    { /* Code for ELEq goes here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
    public R visit(lama.Absyn.EGEq p, A arg)
    { /* Code for EGEq goes here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
    public R visit(lama.Absyn.EEq p, A arg)
    { /* Code for EEq goes here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
    public R visit(lama.Absyn.ENEq p, A arg)
    { /* Code for ENEq goes here */
      p.exp_1.accept(new ExpVisitor<R,A>(), arg);
      p.exp_2.accept(new ExpVisitor<R,A>(), arg);
      return null;
    }
  }
  public class BoolVisitor<R,A> implements lama.Absyn.Bool.Visitor<R,A>
  {
    public R visit(lama.Absyn.Bool_true p, A arg)
    { /* Code for Bool_true goes here */
      return null;
    }
    public R visit(lama.Absyn.Bool_false p, A arg)
    { /* Code for Bool_false goes here */
      return null;
    }
  }
  public class TypeVisitor<R,A> implements lama.Absyn.Type.Visitor<R,A>
  {
    public R visit(lama.Absyn.TInt p, A arg)
    { /* Code for TInt goes here */
      return null;
    }
    public R visit(lama.Absyn.TDouble p, A arg)
    { /* Code for TDouble goes here */
      return null;
    }
    public R visit(lama.Absyn.TBool p, A arg)
    { /* Code for TBool goes here */
      return null;
    }
  }
}
