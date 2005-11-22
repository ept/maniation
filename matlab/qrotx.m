function q = qrotx(alpha)
    % Generate a quaternion for rotating around the x axis by alpha radians

    q = [sin(alpha/2); 0; 0; cos(alpha/2)];
endfunction
