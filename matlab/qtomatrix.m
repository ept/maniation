function retval = qtomatrix(q)
    % Quaternion to rotation matrix
    if (!isquaternion(q))
        usage ("qtomatrix (quaternion)");
    endif

    q = q / qmag(q);
    x = q(1);
    y = q(2);
    z = q(3);
    w = q(4);

    retval = [
        1 - 2*y*y - 2*z*z,      2*x*y + 2*w*z,          2*x*z - 2*w*y;
        2*x*y - 2*w*z,          1 - 2*x*x - 2*z*z,      2*y*z + 2*w*z;
        2*x*z + 2*w*y,          2*y*z - 2*w*x,          1 - 2*x*x - 2*y*y
    ];
endfunction
