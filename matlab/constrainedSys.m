function newstatus = constrainedSys(status, time)

    systemFunc = str2func("pendulum");

    [inertia, invInertia, initStatus, constrFuncs, forcesAndTorques] = systemFunc(status);
    if (rows(status) == 0)
        newstatus = initStatus;
        return;
    endif

    xDot = zeros(6*rows(status)/13, 1);
    for n=1:rows(status)/13
        a=6*(n-1); b=13*(n-1);
        xDot(a+1:a+6) = status(b+8:b+13);
    endfor
    xDot = invInertia*xDot;
    
    for n=1:rows(status)/13
        forcesAndTorques(a+4:a+6) = forcesAndTorques(a+4:a+6) - ...
            cross(xDot(a+4:a+6), status(b+11:b+13));
    endfor

    C = []; Cdot = []; J = []; Jdot = [];
    for n=1:rows(constrFuncs)
        constr = nth(constrFuncs, n);
        [C2, Cdot2, J2, Jdot2] = constr(status, invInertia);
        C = [C;C2]; Cdot = [Cdot;Cdot2]; J = [J;J2]; Jdot = [Jdot;Jdot2];
    endfor

    lambda = (-J*invInertia*J') \ (Jdot*xDot + J*invInertia*forcesAndTorques + C + Cdot);
    forcesAndTorques = forcesAndTorques + J'*lambda;
    
    newstatus = zeros(rows(status), 1);
    
    for n=1:rows(status)/13
        a=6*(n-1); b=13*(n-1);
        newstatus(b+1:b+3) = xDot(a+1:a+3);
        newstatus(b+4:b+7) = qmult([0.5*xDot(a+4:a+6); 0.0], status(b+4:b+7));
        newstatus(b+8:b+13) = forcesAndTorques(a+1:a+6);
    endfor
endfunction
