function retval = plotrot(result, zero, normal)
    % Plot the rotations of all bodies in a simulation result as a function of time.
    % zero: Vector indicating the zero position of the system
    % normal: Normal vector of the plane in which rotation occurs
    
    data=zeros(columns(result), rows(result)/13);
    for n=1:columns(result)
        for m=1:rows(result)/13
            data(n,m) = qtoangle(result(13*m-9:13*m-6, n), zero, normal);
        endfor
    endfor

    plot(1:columns(result), data);
endfunction
