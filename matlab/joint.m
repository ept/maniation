function [C, Cdot, J, Jdot] = joint(status, invInertia)
    % Ball-and-socket joint constraint.
    %
    % status:   column vector of position of centre of mass (3),
    %           orientation (quaternion transforming from local coordinates
    %           to world coordinates), linear momentum (3) and
    %           angular momentum (3) concatenated.

    bodyNumber1 = 1;
    pointInBody1Local = [0;-1;0];
    bodyNumber2 = 2;
    pointInBody2Local = [0;1;0];

    n1 = 13*(bodyNumber1 - 1);  n2 = 13*(bodyNumber2 - 1);
    m1 = 6*(bodyNumber1 - 1);   m2 = 6*(bodyNumber2 - 1);
    a = status(n1+1:n1+3);      b = status(n2+1:n2+3);
    or1 = status(n1+4:n1+7);    or2 = status(n2+4:n2+7);
    mom1 = status(n1+8:n1+10);  mom2 = status(n2+8:n2+10);
    angm1 = status(n1+11:n1+13);angm2 = status(n2+11:n2+13);
    c = invInertia(m1+1:m1+3, m1+1:m1+3) * mom1;
    v = invInertia(m1+4:m1+6, m1+4:m1+6) * angm1;
    d = invInertia(m2+1:m2+3, m2+1:m2+3) * mom2;
    w = invInertia(m2+4:m2+6, m2+4:m2+6) * angm2;

    s = qtransform(or1, pointInBody1Local);
    t = qtransform(or2, pointInBody2Local);

    C = a + s - b - t;
    Cdot = c + cross(v, s) - d - cross(w, t);

    J = Jdot = zeros(3, 6*rows(status)/13);

    J(:,m1+1:m1+6) = [
        1,     0,     0,     0,     s(3), -s(2);
        0,     1,     0,    -s(3),  0,     s(1);
        0,     0,     1,     s(2), -s(1),  0     ];

    J(:,m2+1:m2+6) = [
        -1,    0,     0,     0,    -t(3),  t(2);
        0,     -1,    0,     t(3),  0,    -t(1);
        0,     0,     -1,   -t(2),  t(1),  0     ];

    Jdot(:,m1+4:m1+6) = [
        0,                      v(1)*s(2)-v(2)*s(1),    v(1)*s(3)-v(3)*s(1);
        v(2)*s(1)-v(1)*s(2),    0,                      v(2)*s(3)-v(3)*s(2);
        v(3)*s(1)-v(1)*s(3),    v(3)*s(2)-v(2)*s(3),    0                   ];

    Jdot(:,m2+4:m2+6) = [
        0,                      w(2)*t(1)-w(1)*t(2),    w(3)*t(1)-w(1)*t(3);
        w(1)*t(2)-w(2)*t(1),    0,                      w(3)*t(2)-w(2)*t(3);
        w(1)*t(3)-w(3)*t(1),    w(2)*t(3)-w(3)*t(2),    0                   ];

endfunction
