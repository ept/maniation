function newstatus = torsion_pendulum(status, time)
    % Suggested time step: 3.0
    
    if (rows(status) == 0)
        newstatus = [0;0;0;1;0;0;0;0;0;0;0;0;0.5];
        return;
    endif
    orient = status(4:7);
    pos = qtransform(orient, [1;0;0]);
    angle = acos([1,0,0]*pos);
    if (pos(2) > 0) angle = -angle; endif
    force = qtransform(orient, [0;0.01*angle;0]);
    newstatus = rigidbody(status, time, 1, eye(3)/6.0, [force,-force;pos,-pos]);
endfunction
