function quat = qrot(axis, angle)
    % Construct quaternion to describe rotation of `angle' radians around
    % an arbitrary axis (specified as a 3D direction vector).
    quat = [cos(angle/2); sin(angle/2)*normalize(axis)];
endfunction
