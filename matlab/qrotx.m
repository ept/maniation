function q = qrotx(alpha)
    % Generate a quaternion for rotating around the x axis by alpha radians

    q = [cos(alpha/2); sin(alpha/2); 0; 0];
endfunction
