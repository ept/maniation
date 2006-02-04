function retval = qtransform(q, v)
    % Transform a vector v by the rotation specified by the quaternion q
    r = qmult(q, qmult([0; v], qinv(q)));
    retval = r(2:4);
endfunction
