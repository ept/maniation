function retval = qinv(q)
    % Quaternion inverse
    if (!isquaternion(q))
        usage ("qinv (quaternion)");
    endif

    retval = [ -q(1); -q(2); -q(3); q(4) ] / qmag(q);
endfunction
