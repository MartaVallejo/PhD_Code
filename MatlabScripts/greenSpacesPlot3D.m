function greenSpacesPlot3D()
% Test the 3-D shaded surface plot for collected green prices

cd('../General');
Y = load('NonUrbanPrices.txt'); 
figure
surf(Y, 'FaceColor','interp',...
   'EdgeColor','none',...
   'FaceLighting','phong');
hold on;
xlabel('time steps')
ylabel('max Green Prices')
title('Evolution of the max Green prices','FontSize',12)
legend('random','GA','close','gather','Location','NorthWest')
colormap hot
handle=gcf;
saveas(handle, ['surf', 'eps'],'epsc')
hold off;
cd('../Functions');
end

