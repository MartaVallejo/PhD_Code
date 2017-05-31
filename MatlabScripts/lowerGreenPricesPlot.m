function lowerGreenPricesPlot()
% Create a plot with the collected green prices with the lowest value

cd('../General');
H = load('minGreenPrice.txt');
V = grpstats(H, [H(:,1)]);
Y = load('NonUrbanPrices.txt');
steps = [1:1:600];
C=Y;
C(C==0) = Inf; % Assign zero values to infinite to avoid problems with min
C=min(C(:,2:end),[],2);
plot(steps, V(1,2:end), steps, V(2,2:end),steps, V(3,2:end), steps, C')
hold on;
xlabel('time steps')
ylabel('min Green Prices')
title('Evolution of the min Green prices','FontSize',12)
legend('random','GA','close','gather','Location','NorthWest')
handle=gcf;
saveas(handle, ['minGreenPrices', 'eps'],'epsc')
hold off;
cd('../Functions');
end

