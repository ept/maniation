function newstatus = rigidbody(status, time, mass, invinert, forces)
    % status:   column vector of position of centre of mass (3),
    %           orientation (quaternion transforming from local coordinates
    %           to world coordinates), linear momentum (3) and
    %           angular momentum (3) concatenated.
    % time:     scalar
    % mass:     scalar
    % invinert: 3x3 matrix (inverse of inertia tensor in local coordinates)
    % forces:   matrix with 6 rows and n columns. top 3 rows of each column
    %           are force vector, bottom 3 rows are point of action
    %           (in world coordinates)
    % returns:  same format as status argument, input to ODE solver.
    %           remember to renormalize orientation quaternion after ODE
    %           solver step!

    pos0 = status(1:3);
    orient0 = status(4:7);
    mom0 = status(8:10);
    angmom0 = status(11:13);

    if (columns(forces) == 0) forces = zeros(6,1); endif
    mat0 = qtomatrix(orient0);
    worldinertia = mat0 * invinert * mat0';

    pos1 = mom0/mass;
    angvel0 = worldinertia*angmom0;
    orient1 = qmult([0.0; 0.5*angvel0], orient0);
    mom1 = sum(forces(1:3,:),2);
    angmom1 = [0;0;0];

    for i=1:columns(forces)
        %force = qtransform(qinv(orient0), forces(1:3, i));
        %pos = qtransform(qinv(orient0), forces(4:6, i) - pos0);
        %angmom1 = angmom1 + qtransform(orient0, cross(pos, force));
        force = forces(1:3, i);
        pos = forces(4:6, i) - pos0;
        angmom1 = angmom1 + cross(pos, force);
    endfor
    
    newstatus = zeros(13,1);
    newstatus(1:3) = pos1;
    newstatus(4:7) = orient1;
    newstatus(8:10) = mom1;
    newstatus(11:13) = angmom1;
endfunction
