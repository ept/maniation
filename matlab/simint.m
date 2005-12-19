function newstatus = simint(status, delta)
    % Simulation integration step
    newstatus = status + delta;
    for n=0:floor((rows(status)-1)/13)
        x=13*n+4;
        newstatus(x:x+3) = quergs(status(x:x+3), delta(x:x+3));
    endfor
endfunction
