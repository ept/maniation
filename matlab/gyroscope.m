function newstatus = gyroscope(status, time)

    inertia = eye(3)/6.0;
    invinert = eye(3)*6.0;

    if (rows(status) == 0)
        orient0 = qroty(0.2); % about 11.5 degrees
        angmom0 = qtransform(orient0, inertia*[0;0;20]);
        newstatus = [0;0;0;orient0;0;0;0;angmom0];
        return;
    endif
    
    orient = status(4:7);
    grav = [0;0;-1]; com = [0;0;0]; pos = qtransform(orient, [0;0;-1]);
    
    newstatus = rigidbody(status, time, 1, invinert, [grav,-grav;com,pos]);
endfunction
