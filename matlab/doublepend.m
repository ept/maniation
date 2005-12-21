function [inertia, invInertia, initStatus, constrFuncs, forcesAndTorques] = doublepend(status)
    % Double pendulum!
    inertia = eye(12);
    invInertia = eye(12);
    initStatus = [1/sqrt(2); 1-1/sqrt(2);0;qrotz(pi/4);0;0;0;0;0;0;
                  1/sqrt(2);-1-1/sqrt(2);0;  0;0;0;1;  0;0;0;0;0;0];
    constrFuncs = cell(2,1);
    constrFuncs(1) = str2func("nail");
    constrFuncs(2) = str2func("joint");
    forcesAndTorques = [0;-1;0;0;0;0;
                        0;-1;0;0;0;0];
endfunction
