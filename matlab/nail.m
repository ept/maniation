function [C, Cdot, J, Jdot] = nail(status, invInertia)
    % Nail constraint.
    %
    % status:   column vector of position of centre of mass (3),
    %           orientation (quaternion transforming from local coordinates
    %           to world coordinates), linear momentum (3) and
    %           angular momentum (3) concatenated.

    bodyNumber = 1;
    pointInBodyLocal = [0;1;0];
    pointInWorld = [0;1;0];

    n = 13*(bodyNumber - 1);
    m = 6*(bodyNumber - 1);
    pos = status(n+1:n+3);
    orient = status(n+4:n+7);
    mom = status(n+8:n+10);
    angmom = status(n+11:n+13);
    vel = invInertia(m+1:m+3, m+1:m+3) * mom;
    w = invInertia(m+4:m+6, m+4:m+6) * angmom;

    s = qtransform(orient, pointInBodyLocal);

    C = pos + s - pointInWorld;
    Cdot = vel + cross(w, s);

    J = Jdot = zeros(3, 6*rows(status)/13);

    J(:,m+1:m+6) = [
        1,     0,     0,     0,     s(3), -s(2);
        0,     1,     0,    -s(3),  0,     s(1);
        0,     0,     1,     s(2), -s(1),  0     ];

    Jdot(:,m+4:m+6) = [
        0,                      w(1)*s(2)-w(2)*s(1),    w(1)*s(3)-w(3)*s(1);
        w(2)*s(1)-w(1)*s(2),    0,                      w(2)*s(3)-w(3)*s(2);
        w(3)*s(1)-w(1)*s(3),    w(3)*s(2)-w(2)*s(3),    0                   ];

endfunction
