function retval = simulation(func, timestep, steps)

    status = func([], 0);
    result = zeros(rows(status)+1, steps+1);
    result(:,1) = [0.0; status];

    time = 0.0;
    for n=1:steps
        k1 = timestep*func(status, time);
        k2 = timestep*func(simint(status, k1/2.0), time + timestep/2.0);
        k3 = timestep*func(simint(status, k2/2.0), time + timestep/2.0);
        k4 = timestep*func(simint(status, k3),     time + timestep);
        status = simint(status, k1/6.0 + k2/3.0 + k3/3.0 + k4/6.0);
        time = time + timestep;
        result(:,n+1) = [time; status];
    endfor
    
    retval = result;
endfunction
