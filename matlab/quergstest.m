function result = quergstest()

    duration = 2*pi;
    steps = [11;21;31;41;61;91;121;161;201;281;341;391;461];
    result = zeros(rows(steps), 5);

    for test = 1:rows(steps)
        normEuler = [1;0;0;0]; quergsEuler = [1;0;0;0];
        normRK = [1;0;0;0]; quergsRK = [1;0;0;0];
        h = duration/steps(test);
        for i = 1:steps(test)
            t = duration*(i-1)/steps(test);
            omega1 = [0; 50*cos(t);         0; 0];
            omega2 = [0; 50*cos(t + 0.5*h); 0; 0];
            omega4 = [0; 50*cos(t + h);     0; 0];
            normEuler = normalize(normEuler + h*0.5*qmult(omega1, normEuler));
            quergsEuler = quergs(quergsEuler, h*0.5*qmult(omega1, quergsEuler));
            nk1 = h*0.5*qmult(omega1, normRK);
            nk2 = h*0.5*qmult(omega2, normalize(normRK + 0.5*nk1));
            nk3 = h*0.5*qmult(omega2, normalize(normRK + 0.5*nk2));
            nk4 = h*0.5*qmult(omega4, normalize(normRK +     nk3));
            qk1 = h*0.5*qmult(omega1, quergsRK);
            qk2 = h*0.5*qmult(omega2, quergs(quergsRK, 0.5*qk1));
            qk3 = h*0.5*qmult(omega2, quergs(quergsRK, 0.5*qk2));
            qk4 = h*0.5*qmult(omega4, quergs(quergsRK,     qk3));
            normRK = normalize(normRK + nk1/6.0 + nk2/3.0 + nk3/3.0 + nk4/6.0);
            quergsRK = quergs(quergsRK, qk1/6.0 + qk2/3.0 + qk3/3.0 + qk4/6.0);
        end;
        
        result(test, 1) = h;
        result(test, 2) = abs(asin(normEuler(2)));
        result(test, 3) = abs(asin(quergsEuler(2)));
        result(test, 4) = abs(asin(normRK(2)));
        result(test, 5) = abs(asin(quergsRK(2)));
    end

    loglog(
        result(:,1), result(:,2), "-;euler norm;",
        result(:,1), result(:,3), "-;euler quergs;",
        result(:,1), result(:,4), "-;RK4 norm;",
        result(:,1), result(:,5), "-;RK4 quergs;");
endfunction
