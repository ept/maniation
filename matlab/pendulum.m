function [inertia, invInertia, initStatus, constrFuncs, forcesAndTorques] = pendulum(status)
    inertia = eye(6);
    invInertia = eye(6);
    initStatus = [0;0;0;0;0;0;1;0;0;0;0;0;0.5];
    constrFuncs = cell(1,1);
    constrFuncs(1) = str2func("nail");
    forcesAndTorques = [0;0;0;0;0;0];
endfunction
