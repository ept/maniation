infix 6 power;
infix 5 times;
infix 4 cross;
infix 3 plus;

datatype expr =
    V     of string |
    N     of real |
    times of expr*expr |
    cross of expr*expr |
    plus  of expr*expr |
    power of expr*real |
    ddt   of expr |
    dual  of expr |
    trans of expr;


fun equals (V x) (V y) = (x = y)
  | equals (N x) (N y) = (x = y)
  | equals (a times b) (x times y) =  (equals a x) andalso (equals b y)
  | equals (a cross b) (x cross y) =  (equals a x) andalso (equals b y)
  | equals (a plus  b) (x plus  y) = ((equals a x) andalso (equals b y)) orelse
                                     ((equals a y) andalso (equals a y))
  | equals (a power b) (x power y) =  (equals a x) andalso b=y
  | equals (ddt x    ) (ddt y    ) =  (equals x y)
  | equals (dual x   ) (dual y   ) =  (equals x y)
  | equals (trans x  ) (trans y  ) =  (equals x y)
  | equals _           _           = false;


fun summands (a plus b) = (summands a) @ (summands b)
  | summands a = [a];

fun countInSum(x, y::ys) = if equals x y then 1+(countInSum(x,ys)) else countInSum(x,ys)
  | countInSum _           = 0;

fun combineSummands(prev, x::xs) =
        if countInSum(x,prev) > 0 then combineSummands(prev, xs) else
        let val cnt = countInSum(x,xs) 
        in let val rest = combineSummands(x::prev, xs) and
                   this = (if cnt = 0 then x else ((N (real(1+cnt))) times x))
        in case rest of (N 0.0) => this | _ => (this plus rest) end end
  | combineSummands(_, []) = N 0.0;


fun multExpr (N x, N y) = N(x*y)
  | multExpr (N x, e  ) = if x = 0.0 then N 0.0 else
                          if x = 1.0 then e else (N x) times e
  | multExpr (e,   N x) = if x = 0.0 then N 0.0 else
                          if x = 1.0 then e else (N x) times e
  | multExpr (e,   f  ) = e times f;


fun simplifyFactors ((N x) times e) =
        let val (ep, c) = simplifyFactors e in (ep, multExpr(N x, c)) end
  | simplifyFactors (e times (N x)) =
        let val (ep, c) = simplifyFactors e in (ep, multExpr(N x, c)) end
  | simplifyFactors ((a power x) times e) =
        let val (ap, b) = simplifyFactors a and
                (ep, c) = simplifyFactors e in (ep, multExpr(ap power x, c)) end
  | simplifyFactors (e times (a power x)) =
        let val (ap, b) = simplifyFactors a and
                (ep, c) = simplifyFactors e in (ep, multExpr(ap power x, c)) end
  | simplifyFactors (e times f) =
        let val (ep, c) = simplifyFactors e and
                (fp, d) = simplifyFactors f in (ep times fp, multExpr(c, d)) end
  | simplifyFactors (e cross f) =
        let val (ep, c) = simplifyFactors e and
                (fp, d) = simplifyFactors f in (ep cross fp, multExpr(c, d)) end
  | simplifyFactors (e plus f) =
        let val (ep, c) = simplifyFactors e and
                (fp, d) = simplifyFactors f in
            ((multExpr(ep, c)) plus (multExpr(fp, d)), N 1.0) end
  | simplifyFactors (a power b) =
        let val (ap, c) = simplifyFactors a in
            ((multExpr(ap, c)) power b, N 1.0) end
  | simplifyFactors (ddt a) =
        let val (ap, c) = simplifyFactors a in (ddt ap, c) end
  | simplifyFactors (dual a) =
        let val (ap, c) = simplifyFactors a in (dual ap, c) end
  | simplifyFactors (trans a) =
        let val (ap, c) = simplifyFactors a in (trans a, c) end
  | simplifyFactors e = (e, N 1.0);


fun simplify expr = 
    let fun simp (a times b) = (simp a) times (simp b)
          | simp (a cross b) = (simp a) cross (simp b)
          | simp (a plus b) =
            let val sa = simp a and sb = simp b
            in case (sa,sb) of (N x, N y) => N (x+y)
                             | (N x, e  ) => if x = 0.0 then e else (N x) plus e
                             | (e,   N y) => if y = 0.0 then e else (N y) plus e
                             | _          => combineSummands([], summands(sa plus sb)) end
          | simp (a power b) = (simp a) power b
          | simp (ddt e)     = ddt (simp e)
          | simp (dual e)    = dual (simp e)
          | simp (trans(dual e))    = (N ~1.0) times (dual(simp e))
          | simp (trans(trans e))   = simp e
          | simp (trans(e times f)) = (simp(trans f)) times (simp(trans e))
          | simp (trans(N x))       = N x
          | simp (trans(a power b)) = (simp a) power b
          | simp (trans(e))         = trans(simp e)
          | simp e = e in
    let val (ep, c) = simplifyFactors(simp expr)
    in simp(multExpr(ep, c)) end end;


fun diff ((dn,de)::ds) (V n) = if dn = n then de else (diff ds (V n))
  | diff _  (N n)   = N 0.0
  | diff ds (a times b) = (diff ds a) times b plus a times (diff ds b)
  | diff ds (a cross b) = (diff ds a) cross b plus a cross (diff ds b)
  | diff ds (a plus b)  = (diff ds a) plus  (diff ds b)
  | diff ds (a power b) = (N b) times (a power (b-1.0)) times (diff ds a)
  | diff ds (dual a)    = dual (diff ds a)
  | diff ds (trans a)   = trans (diff ds a)
  | diff _ e = ddt(e);


fun combine f  _       []     =  N 0.0
  | combine f  []      _      =  N 0.0
  | combine f (x::[]) (y::[]) =  f(x,y)
  | combine f (x::xs) (y::[]) = (f(x,y)) plus (combine f xs [y])
  | combine f     xs  (y::ys) = (combine f xs [y]) plus (combine f xs ys);

fun sumOfProducts (a times b) = combine (fn (a,b) => a times b)
        (summands (sumOfProducts a)) (summands (sumOfProducts b))
  | sumOfProducts (a cross b) = combine (fn (a,b) => a cross b)
        (summands (sumOfProducts a)) (summands (sumOfProducts b))
  | sumOfProducts (a plus b) = (sumOfProducts a) plus (sumOfProducts b)
  | sumOfProducts (trans a) = foldr (fn (x,y) => x plus y) (N 0.0)
          (map (fn x => trans x) (summands(sumOfProducts a)))
  | sumOfProducts e = e;


fun exists f [] = false
  | exists f (x::xn) = (f x) orelse (exists f xn);

fun exprInProduct vars e = (exists (equals e) vars) orelse (
    case e of
        (a times b) => (exprInProduct vars a) orelse (exprInProduct vars b)
      | (a cross b) => (exprInProduct vars a) orelse (exprInProduct vars b)
      | (a plus  b) => (exprInProduct vars a) orelse (exprInProduct vars b)
      | (ddt a)     =>  exprInProduct vars a
      | (trans a)   =>  exprInProduct vars a
      | _           => false);

fun varToRight vars (a times b) =
        if (exprInProduct vars a) then
             trans((trans (varToRight vars b)) times (trans(varToRight vars a)))
        else (varToRight vars a) times (varToRight vars b)
  | varToRight vars (a cross b) =
        if (exprInProduct vars a) then
             (N ~1.0) times ((varToRight vars b) cross (varToRight vars a))
        else                ((varToRight vars a) cross (varToRight vars b))
  | varToRight _ e = e;


fun toMatrices (a times b) = (toMatrices a) times (toMatrices b)
  | toMatrices (a cross b) = (dual(toMatrices a)) times (toMatrices b)
  | toMatrices (a plus  b) = (toMatrices a) plus  (toMatrices b)
  | toMatrices (a power b) = (toMatrices a) power b
  | toMatrices (ddt     e) = ddt  (toMatrices e)
  | toMatrices (dual    e) = dual (toMatrices e)
  | toMatrices (trans   e) = trans(toMatrices e)
  | toMatrices e = e;


fun massageVars vars e =
    foldr (fn (x,y) => x plus y) (N 0.0)
        (map (varToRight vars) (summands(sumOfProducts(simplify e))));

fun massage e = simplify(
    massageVars [V "w", ddt(V "w"), V "p", ddt(V "p")] e);

fun print (V n, _) = n
  | print (N n, _) = makestring n
  | print (a times b, level) =
        (if (level > 3) andalso (level <> 5) then "(" else "") ^
        (print(a, 5)) ^ " " ^ (print(b, 5)) ^
        (if (level > 3) andalso (level <> 5) then ")" else "")
  | print (a cross b, level) =
        (if level > 3 then "(" else "") ^
        (print(a, 4)) ^ " x " ^ (print(b,4)) ^
        (if level > 3 then ")" else "")
  | print (a plus b, level) =
        (if level > 3 then "(" else "") ^
        (print(a,3)) ^ " + " ^ (print(b,3)) ^
        (if level > 3 then ")" else "")
  | print (a power b, _) = (print(a,6)) ^ "^" ^ (makestring b)
  | print (ddt a, _) = (print(a,6)) ^ "'"
  | print (dual a, _) = (print(a,6)) ^ "*"
  | print (trans a, _) = (print(a,6)) ^ "T";

fun prettyprint e = print(e,3);


val derivs = [("e", (V "w") cross (V "e")), ("f", (V "p") cross (V "f"))];
val n = (V "e") cross (V "f");
val nm = (trans n) times n;
val nmd = diff derivs nm;
val nhat = n times (((trans n) times n) power ~0.5);
prettyprint(massage nmd);
