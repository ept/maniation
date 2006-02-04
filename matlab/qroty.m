function q = qroty(alpha)
    % Generate a quaternion for rotating around the y axis by alpha radians

    q = [cos(alpha/2); 0; sin(alpha/2); 0];
endfunction
