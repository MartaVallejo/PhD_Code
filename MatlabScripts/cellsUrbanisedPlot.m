function cellsUrbanisedPlot()
% Create a plot with the number of cells urbanised

cd('../General');
H = load('urbanised.txt');
U = grpstats(H, [H(:,1)]);
steps = [1:1:600];
plot(steps, U(1,2:end), steps, U(2,2:end),steps, U(3,2:end))
hold on;
xlabel('time steps')
ylabel('urban cells')
title('Evolution of the urban areas','FontSize',12)
legend('random','GA','close','Location','NorthWest')
handle=gcf;
saveas(handle, ['urbancells', 'eps'],'epsc')
hold off;
cd('../Functions');
end

