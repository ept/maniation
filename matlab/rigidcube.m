function newstatus = rigidcube(status, time)
    orient = status(4:7);
    pos = qtransform(orient, [1;0;0]);
    angle = acos([1,0,0]*pos);
    if (pos(2) > 0) angle = -angle; endif
    force = qtransform(orient, [0;0.01*angle;0]);
    newstatus = rigidbody(status, time, 1, eye(3)/6.0, [force,-force;pos,-pos]);
endfunction
