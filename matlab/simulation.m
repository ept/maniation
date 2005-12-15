function retval = simulation()

    timestep = 0.1;
    steps = 10;

    status = [0;0;0;0;0;0;1;0;0;0;0;0;0];

    time = 0.0;
    dydx = zeros(rows(status),1);
    for i=1:steps
        dym = zeros(rows(status),1);
        dyt = zeros(rows(status),1);
        yt = zeros(rows(status),1);
        dydx = rigidbody
    endfor
    
    status = status + h*rigidbody(status, 0, 1, eye(3), [0;0.1;0;0;status(2);-1]);
    status(4:7) = normalize(status(4:7));
endfunction
