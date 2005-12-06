function retval = qmag(q)
    % Retruns magnitude of a quaternion
    if ((nargin != 1) || !isquaternion(q))
        usage("qmag (quaternion)");
    endif
    retval = sqrt(sumsq(q));
endfunction
