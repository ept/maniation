v1=[0; 0.025; 1.380];
v2=[0.143; 0.061; 1.405];
len=sqrt(sumsq(v2-v1));
r=qtransform(vectortoq([0;1;0], v2-v1, pi*86/180), [0;len;0]) + v1;
v2-r
