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
  | equals (a times b) (x times y) = ((equals a x) andalso (equals b y)) orelse
                                     ((equals a y) andalso (equals b x))
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


fun simpProd (opr, N x,  N y)          = N(x*y)
  | simpProd (opr, N x, (N y) times f) = if x*y = 0.0 then N 0.0 else
                                         if x*y = 1.0 then f else ((N(x*y)) times f)
  | simpProd (opr, N x,  e           ) = if x = 0.0 then N 0.0 else
                                         if x = 1.0 then e     else (N x) times e
  | simpProd (opr, (N x) times e,  N y         ) = if x*y = 0.0 then N 0.0 else
                                                   if x*y = 1.0 then e else
                                                   (N(x*y)) times e
  | simpProd (opr, (N x) times e, (N y) times f) = if x*y = 1.0 then opr(e,f) else
                                                   (N(x*y)) times (opr(e,f))
  | simpProd (opr, (N x) times e,  f           ) = (N x   ) times (opr(e,f))
  | simpProd (opr, e,  N y)          = if y = 0.0 then N 0.0 else
                                       if y = 1.0 then e     else (N y) times e
  | simpProd (opr, e, (N y) times f) = (N y   ) times (opr(e,f))
  | simpProd (opr, e,  f)            = opr(e, f);


fun simplify (a times b) = simpProd((fn (x,y) => x times y), simplify a, simplify b)
  | simplify (a cross b) = simpProd((fn (x,y) => x cross y), simplify a, simplify b)
  | simplify (a plus b) =
        let val sa = simplify a and sb = simplify b
        in case (sa,sb) of (N x, N y) => N (x+y)
                         | (N x, e  ) => if x = 0.0 then e else (N x) plus e
                         | (e,   N y) => if y = 0.0 then e else (N y) plus e
                         | _          => combineSummands([], summands(sa plus sb)) end
  | simplify (a power b) = (simplify a) power b
  | simplify (ddt e)     = let val se = simplify e in
        case se of ((N x) times f) => (N x) times (ddt f)  | _ => (ddt se)  end
  | simplify (dual e)    = let val se = simplify e in
        case se of ((N x) times f) => (N x) times (dual f) | _ => (dual se) end
  | simplify (trans(dual e))    = (N ~1.0) times (dual(simplify e))
  | simplify (trans(trans e))   = simplify e
  | simplify (trans(e times f)) = (simplify(trans f)) times (simplify(trans e))
  | simplify (trans(e))         = trans(simplify e)
  | simplify e = e;


fun diff ((dn,de)::ds) (V n) = if dn = n then de else (diff ds (V n))
  | diff _  (N n)   = N 0.0
  | diff ds (a times b) = (diff ds a) times b plus a times (diff ds b)
  | diff ds (a cross b) = (diff ds a) cross b plus a cross (diff ds b)
  | diff ds (a plus b)  = (diff ds a) plus  (diff ds b)
  | diff ds (a power b) = (N b) times (a power (b-1.0)) times (diff ds a)
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
  | sumOfProducts e = e;


fun exists f [] = false
  | exists f (x::xn) = (f x) orelse (exists f xn);

fun exprInProduct vars e = (exists (equals e) vars) orelse (
    case e of
        (a times b) => (exprInProduct vars a) orelse (exprInProduct vars b)
      | (a cross b) => (exprInProduct vars a) orelse (exprInProduct vars b)
      | (a plus  b) => (exprInProduct vars a) orelse (exprInProduct vars b)
      | (ddt a)     =>  exprInProduct vars a
      | _           => false);

fun varToRight vars (a times b) =
        if (exprInProduct vars a) then
             ((varToRight vars b) times (varToRight vars a))
        else ((varToRight vars a) times (varToRight vars b))
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


fun massage e = simplify(toMatrices(
    foldr (fn (x,y) => x plus y) (N 0.0)
        (map (varToRight [V "w", ddt(V "w"), V "p", ddt(V "p")])
            (summands(sumOfProducts(simplify(e)))))));

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
  | print (a plus b, _) = (print(a,3)) ^ " + " ^ (print(b,3))
  | print (a power b, _) = (print(a,6)) ^ "^" ^ (makestring b)
  | print (ddt a, _) = (print(a,6)) ^ "'"
  | print (dual a, _) = (print(a,6)) ^ "*"
  | print (trans a, _) = (print(a,6)) ^ "T";

fun prettyprint e = print(e,3);


val derivs = [("e", (V "w") cross (V "e")), ("f", (V "p") cross (V "f"))];
val n = (V "e") cross (V "f");
val nhat = n times (((trans n) times n) power ~0.5);
val ndot = simplify(diff derivs n);
val nddot = simplify(sumOfProducts(simplify(diff derivs ndot)));
prettyprint(massage nddot);
prettyprint(massage(diff derivs nhat));
