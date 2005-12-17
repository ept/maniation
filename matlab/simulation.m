function retval = simulation()

    timestep = 3.0;
    steps = 100;

    status = [0;0;0;0;0;0;1;0;0;0;0;0;0.5];
    result = zeros(steps+1, rows(status));
    result(1,:) = status';

    time = 0.0;
    for n=1:steps
        k1 = timestep*rigidcube(status,          time);
        k2 = timestep*rigidcube(status + k1/2.0, time + timestep/2.0);
        k3 = timestep*rigidcube(status + k2/2.0, time + timestep/2.0);
        k4 = timestep*rigidcube(status + k3,     time + timestep);
        status = status + k1/6.0 + k2/3.0 + k3/3.0 + k4/6.0;
        status(4:7) = normalize(status(4:7));
        time = time + timestep;
        result(n+1,:) = status';
    endfor
    
    retval = result;
endfunction
