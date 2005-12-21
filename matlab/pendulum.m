function [inertia, invInertia, initStatus, constrFuncs, forcesAndTorques] = pendulum(status)
    inertia = eye(6);
    invInertia = eye(6);
    initStatus = [1/sqrt(2);1-1/sqrt(2);0;qrotz(pi/4);0;0;0;0;0;0];
    constrFuncs = cell(1,1);
    constrFuncs(1) = str2func("nail");
    forcesAndTorques = [0;-1;0;0;0;0];
endfunction
