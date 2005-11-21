function q = qroty(alpha)
    # Generate a quaternion for rotating around the y axis by alpha radians

    q = [0; sin(alpha/2); 0; cos(alpha/2)];
endfunction
