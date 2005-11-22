function retval = qmag(q)
    % Retruns magnitude of a quaternion
    if ((nargin != 1) || !isquaternion(q))
        usage("qmag (quaternion)");
    endif
    retval = q(1)*q(1) + q(2)*q(2) + q(3)*q(3) + q(4)*q(4)
endfunction
