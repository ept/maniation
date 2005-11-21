function retval = qinterpol(q1, q2, t)
    # Smoothly interpolate between two quaternions
    # (on the unit sphere in quaternion space)
    # t can be a row vector, in which case an interpolation is
    # generated in each column of the result matrix.

    if ((nargin != 3) || !isquaternion(q1) || !isquaternion(q2))
        usage("qinterpol (quaternion1, quaternion2, value)");
    endif
    
    theta = acos(q1(1)*q2(1) + q1(2)*q2(2) + q1(3)*q2(3));
    retval = zeros(4, columns(t));

    for i = 1:columns(t)
        v1 = sin((1-t(i))*theta) / sin(theta);
        v2 = sin(t(i)*theta) / sin(theta);
        retval(1:4,i) = v1*q1 + v2*q2;
    endfor
endfunction
