function retval = simulation()

    timestep = 3.0;
    steps = 100;

    status = [0;0;0;0;0;0;1;0;0;0;0;0;0.5];
    result = zeros(steps+1, rows(status));
    result(1,:) = status';

    time = 0.0;
    for n=1:steps
        k1 = timestep*rigidcube(status, time);
        i2 = status + k1/2.0;
        i2(4:7) = quergs(status(4:7), (k1/2.0)(4:7));
        k2 = timestep*rigidcube(i2, time + timestep/2.0);
        i3 = status + k2/2.0;
        i3(4:7) = quergs(status(4:7), (k2/2.0)(4:7));
        k3 = timestep*rigidcube(i3, time + timestep/2.0);
        i4 = status + k3;
        i4(4:7) = quergs(status(4:7), k3(4:7));
        k4 = timestep*rigidcube(i4, time + timestep);
        kn = k1/6.0 + k2/3.0 + k3/3.0 + k4/6.0;
        new = status + kn;
        new(4:7) = quergs(status(4:7), kn(4:7));
        status = new;
        time = time + timestep;
        result(n+1,:) = status';
    endfor
    
    retval = result;
endfunction
