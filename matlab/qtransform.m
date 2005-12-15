function retval = qtransform(q, v)
    % Transform a vector v by the rotation specified by the quaternion q
    r = qmult(q, qmult([v; 0], qinv(q)));
    retval = r(1:3);
endfunction
