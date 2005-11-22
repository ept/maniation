function q = qrotz(alpha)
    % Generate a quaternion for rotating around the z axis by alpha radians

    q = [0; 0; sin(alpha/2); cos(alpha/2)];
endfunction
