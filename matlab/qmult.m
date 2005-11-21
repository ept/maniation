function prod = qmult(q1, q2)
    # Multiply two quaternions

    if ((nargin != 2) || (rows(q1) != 4) || (rows(q2) != 4))
        usage ("qmult (quaternion1, quaternion2)");
    endif

    if (columns(q1) == 1)
        q1 = repmat(q1, 1, columns(q2));
    elseif (columns(q2) == 1)
        q2 = repmat(q2, 1, columns(q1));
    endif

    n = min(columns(q1), columns(q2));
    prod = zeros(4, n);

    for i = 1:n
        x1 = q1(1,i); y1 = q1(2,i); z1 = q1(3,i); w1 = q1(4,i);
        x2 = q2(1,i); y2 = q2(2,i); z2 = q2(3,i); w2 = q2(4,i);
        prod(:,i) = [
            w1*x2 + w2*x1 + y1*z2 - z1*y2;
            w1*y2 + w2*y1 + z1*x2 - x1*z2;
            w1*z2 + w2*z1 + x1*y2 - y1*x2;
            w1*w2 - x1*x2 - y1*y2 - z1*z2;
        ];
    endfor
endfunction
