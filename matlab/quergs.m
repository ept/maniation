function retval = quergs(q, qdot)
    % Quaternion integration step
    mag = sqrt(sumsq(qdot));
    if (mag < 1e-10)
        retval = q;
    else
        n = round((mag - pi/2.0)/pi);
        d = mag - pi*(n + 0.5);
        if ((d < 1e-5) && (d > -1e-5))
            retval = normalize(qdot);
        else
            retval = normalize(q + qdot*tan(mag)/mag);
        endif
    endif
endfunction
